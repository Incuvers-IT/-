package art.heredium.domain.membership.entity;

import java.time.LocalDate;
import java.util.UUID;

import javax.persistence.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

import org.springframework.lang.Nullable;

import com.vladmihalcea.hibernate.type.json.JsonStringType;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.TypeDef;

import art.heredium.domain.account.entity.Account;
import art.heredium.domain.company.entity.Company;
import art.heredium.domain.ticket.entity.Ticket;

@Entity
@Getter
@Table(name = "membership_registration")
@DynamicInsert
@TypeDef(name = "json", typeClass = JsonStringType.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
// 멤버십등록
public class MembershipRegistration {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "title", length = 100, unique = true)
  private String title;

  @Comment("티켓 uuid")
  @Column(name = "uuid", nullable = false, length = 36, unique = true, updatable = false)
  private String uuid;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "account_id", nullable = false)
  private Account account;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "membership_id")
  private Membership membership;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_id")
  private Company company;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ticket_id")
  private Ticket ticket;

  @Comment("가입일시")
  @Column(name = "registration_date", nullable = false)
  private LocalDate registrationDate;

  @Comment("만료일시")
  @Column(name = "expiration_date", nullable = false)
  private LocalDate expirationDate;

  @Comment("결제 상태")
  @Column(name = "payment_status")
  @Enumerated(EnumType.STRING)
  private PaymentStatus paymentStatus;

  @Column(name = "payment_date", nullable = false)
  private LocalDate paymentDate;

  @Comment("등록 유형")
  @Column(name = "registration_type", nullable = false)
  private RegistrationType registrationType;

  public MembershipRegistration(
      @NonNull Account account,
      @NonNull Membership membership,
      @NonNull LocalDate registrationDate,
      @NonNull LocalDate expirationDate,
      @NonNull LocalDate expirationDate,
      @NonNull RegistrationType registrationType,
      @NonNull PaymentStatus paymentStatus,
      @NonNull LocalDate paymentDate,
      @Nullable Ticket ticket)) {
    this.uuid = UUID.randomUUID().toString();
    this.account = account;
    this.membership = membership;
    this.registrationDate = registrationDate;
    this.expirationDate = expirationDate;
    this.registrationType = registrationType;
    this.paymentStatus = paymentStatus;
    this.paymentDate = paymentDate;
    this.ticket = ticket;
  }

    public MembershipRegistration(
            @NonNull Account account,
            @NonNull Company company,
            @NonNull LocalDate registrationDate,
            @NonNull LocalDate expirationDate,
            @NonNull RegistrationType registrationType,
            @Nullable Ticket ticket) {
        this.uuid = UUID.randomUUID().toString();
        this.account = account;
        this.company = company;
        this.registrationDate = registrationDate;
        this.expirationDate = expirationDate;
        this.registrationType = registrationType;
        this.paymentStatus = paymentStatus;
        this.paymentDate = paymentDate;
        this.ticket = ticket;
    }
}
