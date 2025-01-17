package art.heredium.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import art.heredium.core.config.error.entity.ApiException;
import art.heredium.core.config.error.entity.ErrorCode;
import art.heredium.domain.post.entity.PostHistory;
import art.heredium.domain.post.model.dto.request.PostHistorySearchRequest;
import art.heredium.domain.post.model.dto.response.PostHistoryBaseResponse;
import art.heredium.domain.post.model.dto.response.PostHistoryResponse;
import art.heredium.domain.post.repository.PostHistoryRepository;
import art.heredium.domain.post.repository.PostHistoryRepositoryImpl;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostHistoryService {
  private PostHistoryRepository postHistoryRepository;
  private PostHistoryRepositoryImpl postHistoryRepositoryImpl;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Async
  @Transactional(rollbackFor = Exception.class)
  public void save(PostHistory entity) {
    this.postHistoryRepository.save(entity);
  }

  public Page<PostHistoryBaseResponse> listPostHistory(
      PostHistorySearchRequest request, Pageable pageable) {
    return this.postHistoryRepositoryImpl.search(request, pageable);
  }

  public PostHistoryResponse getPostHistory(Long postHistoryId) {
    PostHistory postHistory =
        this.postHistoryRepository
            .findById(postHistoryId)
            .orElseThrow(
                () -> new ApiException(ErrorCode.POST_HISTORY_NOT_FOUND, "Post history not found"));
    return PostHistoryResponse.builder()
        .postHistoryId(postHistory.getId())
        .modifiedDate(postHistory.getLastModifiedDate())
        .modifyUserEmail(postHistory.getModifyUserEmail())
        .modifyUserName(postHistory.getLastModifiedName())
        .content(postHistory.getPostContent())
        .build();
  }
}
