package com.gaetan.virus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Main {
    private static final Queue<Guys> guys = new ConcurrentLinkedQueue<>();
    private static final Queue<Infected> canInfect = new ConcurrentLinkedQueue<>();
    private static final Queue<Infected> notInfect = new ConcurrentLinkedQueue<>();
    private static int stage = 0;

    public static void main(final String[] args) {
        virus(Collections.singletonList(new int[]{1, 1}), 3);
    }

    public static List<Integer> virus(final List<int[]> patients0, final int n) {
        final long ms = System.currentTimeMillis();

        for (int x = 0; x < n; ++x) {
            for (int y = 0; y < n; ++y) {
                final int finalX = x;
                final int finalY = y;

                patients0.parallelStream()
                        .filter(pat -> !(pat[0] > n || pat[1] > n))
                        .forEach(pat -> {
                            if (pat.length == 2 && pat[0] == finalX && pat[1] == finalY)
                                canInfect.add(new Infected(finalX, finalY));
                            else guys.add(new Guys(finalX, finalY));
                        });
            }

        }

        if (guys.size() == 0 || canInfect.size() == 0)
            return null;

        System.out.println("(J-" + stage + ") " + "Habitants: " + guys.size() + " ; Infectés: " + (canInfect.size() + notInfect.size()));

        boolean dead = false;
        int counter = 0;

        while (!dead) {
            if (counter != stage)
                return null;

            next(++counter);

            if (guys.size() == 0) {
                dead = true;
                System.out.println("Il ne reste plus personne (DEBUG:" + (System.currentTimeMillis() - ms) + "ms)");
            }
        }
        return Collections.singletonList(canInfect.size());
    }

    public static void next(final int counter) {
        final List<Infected> ptoRemove = Collections.synchronizedList(new ArrayList<>());
        final List<Infected> ptoAdd = Collections.synchronizedList(new ArrayList<>());

        canInfect.parallelStream()
                .forEach(patient -> {
                    final List<Guys> gtoRemove = new ArrayList<>();
                    boolean isInfected = false;

                    for (final Guys guy : guys) {
                        if ((guy.getX() + 1 == patient.getX() && guy.getY() == patient.getY()) ||
                                (guy.getX() == patient.getX() && (guy.getY() - 1 == patient.getY())) ||
                                (guy.getX() - 1 == patient.getX() && guy.getY() == patient.getY()) ||
                                (guy.getX() == patient.getX() && guy.getY() + 1 == patient.getY())) {

                            ptoAdd.add(new Infected(guy.getX(), guy.getY()));
                            gtoRemove.add(guy);
                            isInfected = true;
                        }
                    }

                    guys.removeAll(gtoRemove);
                    if (!isInfected) {
                        ptoRemove.add(patient);
                        notInfect.add(patient);
                    }

                });

        canInfect.addAll(ptoAdd);
        canInfect.removeAll(ptoRemove);
        ++stage;

        System.out.println("(J-" + stage + ") " + "Habitants: " + guys.size() + " ; Infectés: " + (canInfect.size() + notInfect.size()));
    }
}