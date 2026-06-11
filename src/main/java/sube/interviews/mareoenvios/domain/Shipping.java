package sube.interviews.mareoenvios.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shipping")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"customer", "items"})
public class Shipping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    @Builder.Default
    private ShippingState state = ShippingState.INITIAL;

    @Column(name = "send_date")
    private LocalDate sendDate;

    @Column(name = "arrive_date")
    private LocalDate arriveDate;

    @Column(name = "priority", nullable = false)
    @Builder.Default
    private Integer priority = 1;

    @OneToMany(mappedBy = "shipping", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<ShippingItem> items = new ArrayList<>();

    public void transitionTo(ShippingState newState) {
        this.state.validateTransition(newState);
        this.state = newState;
    }
}
