package art.heredium.core.util;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import art.heredium.domain.account.entity.Account;
import art.heredium.domain.account.entity.Admin;
import art.heredium.domain.account.entity.UserPrincipal;

public class AuthUtil {
  public static Optional<Long> getCurrentUserAccountId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication.getPrincipal() instanceof UserPrincipal)) {
      return Optional.empty();
    }
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
    return Optional.ofNullable(userPrincipal.getAccount()).map(Account::getId);
  }

  public static Optional<Admin> getCurrentAdmin() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
      UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
      return Optional.ofNullable(userPrincipal.getAdmin());
    }
    return Optional.empty();
  }
}
