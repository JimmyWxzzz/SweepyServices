package com.sweepy.urlService;

import com.sweepy.RedisCache.SequenceIdService;
import com.sweepy.RedisCache.RedisService;
import com.sweepy.converter.Base62Converter;
import com.sweepy.converter.RandomConverter;
import com.sweepy.database.UrlTable;
import com.sweepy.exception.EmptyEntry;
import com.sweepy.exception.NullRequest;
import com.sweepy.repository.UrlRepository;
import org.apache.commons.validator.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Objects;

@Service
public class UrlServiceImpl implements UrlService {

    @Autowired
    private UrlRepository urlRepository;
    @Autowired
    private RedisTemplate<String, Long> redisTemplateId;
    private RedisService redisService;
    long defaultTime = 5;

    private String protocol = "http://";


    private Base62Converter base62Converter = new Base62Converter();
    private RandomConverter randomConverter = new RandomConverter();


    public UrlServiceImpl(UrlRepository urlRepository, RedisService redisService) {
        this.urlRepository = urlRepository;
        this.redisService = redisService;
    }

    @Override
    @Transactional
    public String longToShort(String longUrl, String method) {
        String shortUrl = "";
        UrlTable entry = null;

        String shortInRedis = (String) redisService.get(longUrl);
        if (shortInRedis == null) {
            entry = urlRepository.findByLongUrl(longUrl);

            if (!isValid(protocol + longUrl)) {
                return "You must enter a valid long Url.";
            } else if (entry == null || !Objects.equals(entry.getMethod(), method)) {
//                SequenceIdService ser = new SequenceIdService(redisTemplateId);
//                Long Id = ser.getNextSequenceIdbyAtomic();
                UrlTable maxIdItem = urlRepository.findFirstByOrderByIdDesc();
                long Id = maxIdItem.getId();
                if (Objects.equals(method, "base62")) {
                    shortUrl = base62Converter.encode(Math.toIntExact(Id));
                } else if (Objects.equals(method, "random")) {
                    shortUrl = randomConverter.encode(longUrl);
                } else {
                    return "Please enter a valid encode method.";
                }



                entry = new UrlTable(Id,longUrl, shortUrl, method);

                saveToRedis(longUrl, shortUrl, defaultTime);
                urlRepository.save(entry);
                return shortUrl;
            } else {

                shortUrl = entry.getShortUrl();
                saveToRedis(longUrl, shortUrl, defaultTime);
                return shortUrl;
            }
        } else {
            if (isValid(protocol + longUrl)) {
                redisService.expire(longUrl, defaultTime);
                return shortInRedis;
            }
            return "You must enter a valid long Url.";

        }

    }

    @Override
    @Transactional
    public String shortToLong(String shortUrl, HttpServletResponse response) throws IOException {
        UrlTable entry;
        String longUrl;

        String longInRedis = (String) redisService.get(shortUrl);

        if (longInRedis == null) {


            try {
                entry = urlRepository.findByShortUrl(shortUrl);
                if (shortUrl == "") {
                    throw new EmptyEntry("You must enter a valid short Url.");

                }
                if (entry == null) {
                    throw new NullRequest("The short Url: " + shortUrl + " is invalid");
                }
                longUrl = protocol + entry.getLongUrl();
                if (isValid(longUrl)) {
                    response.sendRedirect(longUrl);
                    saveToRedis(entry.getLongUrl(), shortUrl, defaultTime);
                }

            } catch (NullRequest | EmptyEntry | IOException e) {
                return e.getMessage();
            }
        } else {
            longUrl = protocol + longInRedis;
            if (isValid(longUrl)) {
                response.sendRedirect(longUrl);
            } else {
                return "Please enter a valid url";
            }
            redisService.expire(longInRedis, defaultTime);
        }


        return null;
    }



    private void saveToRedis(String longUrl, String shortUrl, long time) {
        System.out.println("outside");
        redisService.setLTS(longUrl, shortUrl, time);
        System.out.println("inlong");
        redisService.setSTL(longUrl, shortUrl, time);
        System.out.println("inshort");
    }

    private String getShortUrl(String longUrl) {
        String shortUrl = (String) redisService.get(longUrl);
        if (shortUrl != null) {
            redisService.expire(longUrl, 5);
        }
        return shortUrl;
    }


    private String getLongUrl(String shortUrl) {
        String longUrl = (String) redisService.get(shortUrl);
        if (longUrl != null) {
            redisService.expire(shortUrl, 5);
        }
        return longUrl;
    }

    private boolean isValid(String url)
    {
        UrlValidator urlValidator = new UrlValidator();
        return urlValidator.isValid(url);
    }
}
