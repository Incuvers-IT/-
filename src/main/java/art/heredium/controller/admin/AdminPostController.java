package art.heredium.controller.admin;

import java.util.List;

import javax.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import art.heredium.core.annotation.ManagerPermission;
import art.heredium.core.config.error.entity.ApiException;
import art.heredium.core.config.error.entity.ErrorCode;
import art.heredium.domain.common.model.dto.response.CustomPageResponse;
import art.heredium.domain.membership.model.dto.request.MultipleMembershipCreateRequest;
import art.heredium.domain.membership.model.dto.response.MultipleMembershipCreateResponse;
import art.heredium.domain.post.model.dto.request.GetAdminPostRequest;
import art.heredium.domain.post.model.dto.request.PostCreateRequest;
import art.heredium.domain.post.model.dto.request.PostUpdateRequest;
import art.heredium.domain.post.model.dto.response.PostResponse;
import art.heredium.service.MembershipService;
import art.heredium.service.PostService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/posts")
@ManagerPermission
public class AdminPostController {

  private final MembershipService membershipService;
  private final PostService postService;

  @PostMapping("/{post_id}/membership/add")
  public ResponseEntity<MultipleMembershipCreateResponse> createMemberships(
      @PathVariable(name = "post_id") Long postId,
      @RequestBody @Valid MultipleMembershipCreateRequest request) {
    final List<Long> membershipIds =
        membershipService.createMemberships(postId, request.getMemberships());
    return ResponseEntity.ok(new MultipleMembershipCreateResponse(membershipIds));
  }

  @PutMapping("/{post-id}/update-is-enabled")
  public ResponseEntity updateIsEnabled(
      @PathVariable("post-id") long postId, @RequestBody PostUpdateRequest request) {
    this.postService.updateIsEnabled(postId, request.getIsEnabled());
    return ResponseEntity.ok().build();
  }

  @PostMapping
  public ResponseEntity<Long> createPost(@Valid @RequestBody PostCreateRequest request) {
    if (!request.getIsEnabled() && request.getMemberships() != null) {
      throw new ApiException(
          ErrorCode.BAD_VALID, "Memberships cannot be created for disabled posts");
    }

    Long postId = postService.createPost(request);

    return ResponseEntity.ok(postId);
  }

  @GetMapping
  public ResponseEntity<CustomPageResponse<PostResponse>> list(
      @Valid GetAdminPostRequest dto, Pageable pageable) {
    Pageable adjustedPageable =
        PageRequest.of(
            Math.max(0, pageable.getPageNumber() - 1), pageable.getPageSize(), pageable.getSort());

    Page<PostResponse> page = postService.list(dto, adjustedPageable);

    CustomPageResponse<PostResponse> response = new CustomPageResponse<>(page);
    response.setCurrentPage(pageable.getPageNumber());

    return ResponseEntity.ok(response);
  }
}
