package art.heredium.domain.membership.model.dto.request;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import art.heredium.domain.coupon.model.dto.request.MembershipCouponCreateRequest;

@Getter
@Setter
public class MembershipCreateRequest {

  private static final Long DEFAULT_MEMBERSHIP_PERIOD = 12L;

  @NotBlank(message = " can not be blank")
  private String name;

  private Long period = DEFAULT_MEMBERSHIP_PERIOD;

  @NotNull(message = " can not be null")
  private Integer price;

  @NotEmpty(message = " can not be empty")
  @Valid
  private List<MembershipCouponCreateRequest> coupons;
}
