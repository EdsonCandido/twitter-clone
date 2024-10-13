package com.edson.apispring.controller;

import com.edson.apispring.controller.dto.CreateTweetDto;
import com.edson.apispring.controller.dto.FeedDto;
import com.edson.apispring.controller.dto.FeedItensDto;
import com.edson.apispring.entitis.Tweet;
import com.edson.apispring.repository.TweetRepository;
import com.edson.apispring.repository.UserRepository;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
public class TweetController {

    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;

    public TweetController(TweetRepository tweetRepository, UserRepository userRepository) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
    }


    @GetMapping("/tweets")
    public ResponseEntity<FeedDto> feed(@RequestParam(value="page", defaultValue = "0") int page,
                                        @RequestParam(value="pageSize", defaultValue = "10") int pageSize){

      var tweets =  this.tweetRepository.findAll(PageRequest.of(page, pageSize, Sort.Direction.DESC, "createdAt" ))
              .map(tweet -> new FeedItensDto(tweet.getTweetId(), tweet.getContent(), tweet.getUser().getUsername()));

      return ResponseEntity.ok(new FeedDto(tweets.getContent(), page, pageSize, tweets.getTotalPages(), tweets.getTotalElements()));
    }

    @PostMapping("/tweets")
    public ResponseEntity<Tweet> createTweet(@RequestBody CreateTweetDto dto, JwtAuthenticationToken token) {

        var user = this.userRepository.findById(Long.parseLong(token.getName()));

        var newTweet = new Tweet();

        newTweet.setUser(user.get());
        newTweet.setContent(dto.content());

        this.tweetRepository.save(newTweet);

        return ResponseEntity.status(HttpStatus.CREATED).body(newTweet);

    }

    @DeleteMapping("/tweets/{id}")
    public ResponseEntity<Void> deleteTweet(@PathVariable("id") Long tweetId, JwtAuthenticationToken token) {

        var tweet = this.tweetRepository.findById(tweetId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        if (tweet.getUser().getUserId() == Long.parseLong(token.getName())) {
            this.tweetRepository.deleteById(tweetId);
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok().build();
    }


}
