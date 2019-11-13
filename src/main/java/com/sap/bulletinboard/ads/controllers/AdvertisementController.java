package com.sap.bulletinboard.ads.controllers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sap.bulletinboard.ads.models.Advertisement;

/*
 * Use a path which does not end with a slash! Otherwise the controller is not reachable when not using the trailing
 * slash in the URL
 */
@RestController
@RequestMapping(path = AdvertisementController.PATH)
@RequestScope // @Scope(WebApplicationContext.SCOPE_REQUEST)
public class AdvertisementController {
    public static final String PATH = "/api/v1/ads";

    private static final Map<Long, Advertisement> ads = new HashMap<>();

    @GetMapping
    public AdvertisementList advertisements() {
        return new AdvertisementList(ads.values());
    }

    @GetMapping("/{id}")
    // We do not use primitive "long" type here to avoid unnecessary autoboxing
    public Advertisement advertisementById(@PathVariable("id") Long id) {
        if (!ads.containsKey(id)) {
            throw new NotFoundException(id + " not found");
        }
        return ads.get(id);
    }

    /**
     * @RequestBody is bound to the method argument. HttpMessageConverter resolves method argument depending on the
     *              content type.
     */
    @PostMapping
    public ResponseEntity<Advertisement> add(@RequestBody Advertisement advertisement,
                                             UriComponentsBuilder uriComponentsBuilder) throws URISyntaxException {

        long id = ads.size();
        ads.put(id, advertisement);

        UriComponents uriComponents = uriComponentsBuilder.path(PATH + "/{id}").buildAndExpand(id);
        return ResponseEntity.created(new URI(uriComponents.getPath())).body(advertisement);
    }

    public static class AdvertisementList {
        @JsonProperty("value")
        public List<Advertisement> advertisements = new ArrayList<>();

        public AdvertisementList(Iterable<Advertisement> ads) {
            ads.forEach(advertisements::add);
        }
    }
}