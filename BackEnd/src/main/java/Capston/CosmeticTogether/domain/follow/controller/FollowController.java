package Capston.CosmeticTogether.domain.follow.controller;

import Capston.CosmeticTogether.domain.follow.service.FollowService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1")
public class FollowController {
    private final FollowService followService;

    @PostMapping("/follow/{followingId}")
    @Operation(summary = "상대방 팔로우 하기", description = "상대방의 id를 전달하면 팔로우가 됩니다  / 토큰 필요")
    public ResponseEntity<String> followMember(@PathVariable("followingId") Long followingId) {
        String followingMemberNickName = followService.followMember(followingId);
        return ResponseEntity.ok(followingMemberNickName + " 님을 팔로우하였습니다");
    }

    @PostMapping("/unfollow/{unfollowingId}")
    @Operation(summary = "상대방 언팔로우 하기", description = "상대방의 id를 전달하면 언팔로우가 됩니다  / 토큰 필요")
    public ResponseEntity<String> unfollowMember(@PathVariable("unfollowingId") Long unfollowingId) {
        String unfollowingMemberNickName = followService.unfollowMember(unfollowingId);
        return ResponseEntity.ok(unfollowingMemberNickName + " 님을 팔로우 취소하였습니다");
    }

    //TODO 사진 같이 전달
    @GetMapping("/followers")
    @Operation(summary = "본인의 팔로워 가져오기", description = "본인의 팔로워를 사진, 이름과 함께 전달합니다  / 토큰 필요")
    public ResponseEntity<List<String>> getFollowers() {
        List<String> followerMemberList = followService.getFollowers();
        return ResponseEntity.ok(followerMemberList);
    }

    @GetMapping("/followings")
    @Operation(summary = "본인의 팔로잉 가져오기", description = "본인의 팔로잉을 사진, 이름과 함께 전달합니다  / 토큰 필요")
    public ResponseEntity<List<String>> getFollowings() {
        List<String> followingMemberList = followService.getFollowings();
        return ResponseEntity.ok(followingMemberList);
    }
}
