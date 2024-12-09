package com.competitionmathacademy.mathapi.controller;

import com.competitionmathacademy.mathapi.model.Post;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import com.competitionmathacademy.mathapi.repository.PostRepository;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @PostMapping("/create-post")
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        post.setUpdatedAt(currentTime);
        post.setCreatedAt(currentTime);
        Post savedPost = postRepository.save(post);
        return ResponseEntity.ok(savedPost);
    }

    @GetMapping("/get-post/{id}")
    public ResponseEntity<Post> getPost(@PathVariable Long id) {
        return postRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public List<Post> getAllPostsOrdered() {
        return postRepository.findAllByOrderByUpdatedAtDesc();
    }

    @DeleteMapping("/delete-post/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id, @RequestParam String username) {
        Post post = postRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        if (!post.getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to delete this post");
        }
        postRepository.delete(post);
        return ResponseEntity.noContent().build();
    }
}
