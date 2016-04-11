/*-
 * Copyright (c) 2016, NGSPipes Team <ngspipes@gmail.com>
 * All rights reserved.
 *
 * This file is part of NGSPipes <http://ngspipes.github.io/>.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ngspipesengine.core.configurator.engines;


import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class VMManager {

    public static final String BASE_VM_NAME = "NGSPipesEngineExecutor";
    protected static final List<String> REGISTERED_NAMES = new LinkedList<>();
    protected static final Object LOCK = new Object();


    public static String register(String vmName) {
        synchronized (LOCK) {
            if(REGISTERED_NAMES.contains(vmName) || vmName.equals(BASE_VM_NAME))
                vmName = getName();

            REGISTERED_NAMES.add(vmName);
            return vmName;
        }
    }

    public static void unregister(String vmName) {
        synchronized (LOCK) {
            if(REGISTERED_NAMES.contains(vmName))
                REGISTERED_NAMES.remove(vmName);
        }
    }


    private static String getName() {
        if(REGISTERED_NAMES.size() == 0)
            return BASE_VM_NAME + "0";
        return BASE_VM_NAME + getNextNumber();
    }

    private static int getNextNumber() {
        int[] usedNumbers = getRegisteredNumbers();
        Arrays.sort(usedNumbers);

        for(int idx = 0; idx < usedNumbers.length - 1; idx++) {
            if (usedNumbers[idx] - usedNumbers[idx + 1] > -1)
                return usedNumbers[idx] + 1;
        }

        return usedNumbers[usedNumbers.length - 1] + 1;
    }

    private static int[] getRegisteredNumbers() {
        int[] registeredNumbers = new int[REGISTERED_NAMES.size()];

        int idx = 0;
        for (String name : REGISTERED_NAMES) {
            name = name.replace(BASE_VM_NAME, "");
            registeredNumbers[idx++] = Integer.parseInt(name);
        }

        return registeredNumbers;
    }

}
