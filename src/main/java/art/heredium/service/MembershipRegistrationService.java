package art.heredium.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import art.heredium.core.config.error.entity.ApiException;
import art.heredium.core.config.error.entity.ErrorCode;
import art.heredium.core.util.AuthUtil;
import art.heredium.domain.account.entity.Account;
import art.heredium.domain.account.repository.AccountRepository;
import art.heredium.domain.coupon.entity.CouponSource;
import art.heredium.domain.coupon.entity.CouponUsage;
import art.heredium.domain.coupon.repository.CouponUsageRepository;
import art.heredium.domain.membership.entity.Membership;
import art.heredium.domain.membership.entity.MembershipRegistration;
import art.heredium.domain.membership.entity.PaymentStatus;
import art.heredium.domain.membership.entity.RegistrationType;
import art.heredium.domain.membership.model.dto.response.MembershipRegistrationResponse;
import art.heredium.domain.membership.model.dto.response.RegisterMembershipResponse;
import art.heredium.domain.membership.repository.MembershipRegistrationRepository;
import art.heredium.domain.membership.repository.MembershipRepository;
import art.heredium.domain.post.entity.Post;
import art.heredium.domain.post.repository.PostRepository;

import static art.heredium.core.config.error.entity.ErrorCode.ANONYMOUS_USER;

@Service
@RequiredArgsConstructor
public class MembershipRegistrationService {

  private final MembershipRegistrationRepository membershipRegistrationRepository;
  private final MembershipRepository membershipRepository;
  private final PostRepository postRepository;
  private final AccountRepository accountRepository;
  private final CouponUsageRepository couponUsageRepository;
  private final CouponUsageService couponUsageService;

  public MembershipRegistrationResponse getMembershipRegistrationInfo() {
    final long accountId =
        AuthUtil.getCurrentUserAccountId().orElseThrow(() -> new ApiException(ANONYMOUS_USER));
    final MembershipRegistration membershipRegistration =
        this.membershipRegistrationRepository
            .findByAccountIdAndNotExpired(accountId)
            .orElseThrow(() -> new ApiException(ErrorCode.MEMBERSHIP_REGISTRATION_NOT_FOUND));
    final List<CouponUsage> couponUsages =
        this.couponUsageRepository.findByAccountIdAndIsUsedFalseAndNotExpiredAndSource(
            accountId, CouponSource.MEMBERSHIP_PACKAGE);
    return new MembershipRegistrationResponse(membershipRegistration, couponUsages);
  }

  @Transactional(rollbackFor = Exception.class)
  public RegisterMembershipResponse registerMembership(long membershipId) {
    final long accountId =
        AuthUtil.getCurrentUserAccountId()
            .orElseThrow(() -> new ApiException(ErrorCode.ANONYMOUS_USER));
    final Optional<MembershipRegistration> existingMembershipRegistration =
        this.membershipRegistrationRepository.findByAccountIdAndNotExpired(accountId);
    if (existingMembershipRegistration.isPresent()) {
      throw new ApiException(ErrorCode.MEMBERSHIP_REGISTRATION_ALREADY_EXISTS);
    }
    final Membership membership =
        this.membershipRepository
            .findByIdAndIsEnabledTrue(membershipId)
            .orElseThrow(() -> new ApiException(ErrorCode.MEMBERSHIP_NOT_FOUND));
    final Post post =
        this.postRepository
            .findByMembershipIdAndIsEnabledTrue(membershipId)
            .orElseThrow(() -> new ApiException(ErrorCode.POST_NOT_FOUND));
    if (post.getOpenDate() != null && post.getOpenDate().isAfter(LocalDate.now())) {
      throw new ApiException(ErrorCode.REGISTERING_MEMBERSHIP_IS_NOT_AVAILABLE);
    }
    final Account account =
        this.accountRepository
            .findById(accountId)
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
    final String paymentOrderId = UUID.randomUUID().toString();
    final MembershipRegistration membershipRegistration =
        this.membershipRegistrationRepository.save(
            new MembershipRegistration(
                account,
                membership,
                RegistrationType.MEMBERSHIP_PACKAGE,
                PaymentStatus.PENDING,
                paymentOrderId));
    return new RegisterMembershipResponse(membershipRegistration);
  }
}
