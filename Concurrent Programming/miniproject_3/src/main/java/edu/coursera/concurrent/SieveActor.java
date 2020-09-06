package edu.coursera.concurrent;

import edu.rice.pcdp.Actor;

import static edu.rice.pcdp.PCDP.finish;

/**
 * An actor-based implementation of the Sieve of Eratosthenes.
 */
public final class SieveActor extends Sieve {
    /**
     * {@inheritDoc}
     * We use the SieveActorActor class to calculate the number of primes <=
     * limit in parallel.
     */
    @Override
    public int countPrimes(final int limit) {
        final SieveActorActor actor = new SieveActorActor(2);
        finish(() -> {
            for (int i = 3; i <= limit; i += 2) {
                actor.send(i);
            }
        });

        int numPrimes = 0;
        SieveActorActor loopActor = actor;
        while (loopActor != null) {
            loopActor = loopActor.nextActor;
            numPrimes++;
        }

        return numPrimes;
    }

    /**
     * An actor class that helps implement the Sieve of Eratosthenes in
     * parallel.
     */
    public static final class SieveActorActor extends Actor {
        private int prime;
        private SieveActorActor nextActor;

        SieveActorActor(final int prime) {
            this.prime = prime;
        }

        /**
         * Process a single message sent to this actor.
         *
         * @param msg Received message
         */
        @Override
        public void process(final Object msg) {
            int candidate = (Integer) msg;
            if (candidate % prime != 0) {
                if (nextActor == null)
                    nextActor = new SieveActorActor(candidate);
                else
                    nextActor.send(msg);
            }
        }
    }
}
