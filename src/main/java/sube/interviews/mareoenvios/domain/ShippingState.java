package sube.interviews.mareoenvios.domain;

import sube.interviews.mareoenvios.exception.InvalidStateTransitionException;

import java.util.Set;

public enum ShippingState {

    INITIAL {
        @Override
        public Set<ShippingState> allowedTransitions() {
            return Set.of(SENT_TO_MAIL, CANCELLED);
        }
    },
    SENT_TO_MAIL {
        @Override
        public Set<ShippingState> allowedTransitions() {
            return Set.of(IN_TRAVEL, CANCELLED);
        }
    },
    IN_TRAVEL {
        @Override
        public Set<ShippingState> allowedTransitions() {
            return Set.of(DELIVERED);
        }
    },
    DELIVERED {
        @Override
        public Set<ShippingState> allowedTransitions() {
            return Set.of();
        }
    },
    CANCELLED {
        @Override
        public Set<ShippingState> allowedTransitions() {
            return Set.of();
        }
    };

    public abstract Set<ShippingState> allowedTransitions();

    public void validateTransition(ShippingState next) {
        if (!allowedTransitions().contains(next)) {
            throw new InvalidStateTransitionException(
                    String.format("Cannot transition from %s to %s", this.name(), next.name())
            );
        }
    }
}
