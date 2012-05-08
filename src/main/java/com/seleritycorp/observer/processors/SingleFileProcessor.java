/*
 * (c) Copyright Selerity, Inc. 2009-2012. All rights reserved. This source code is confidential 
 * and proprietary information of Selerity Inc. and may be used only by a recipient designated 
 * by and for the purposes permitted by Selerity Inc. in writing.  Reproduction of, dissemination 
 * of, modifications to or creation of derivative works from this source code, whether in source 
 * or binary forms, by any means and in any form or manner, is expressly prohibited, except with 
 * the prior written permission of Selerity Inc..  THIS CODE AND INFORMATION ARE PROVIDED "AS IS"
 * WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE. This notice may not be 
 * removed from the software by any user thereof.
 */

package com.seleritycorp.observer.processors;

import com.seleritycorp.datatypes.EnrichedObservation;
import com.seleritycorp.observer.Config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * A processor that writes the observation out to a uniquely named file.
 */
public class SingleFileProcessor extends Processor {
    private static final String INVALID_CHARS = "[^0-9a-zA-Z]";
    private static long counter = 0;
    private final File output;

    public SingleFileProcessor() {
        output = new File(Config.PROCESSOR_SINGLE_FILE_OUTPUT.getProperty());

        if (output.exists()) {

            if (!(output.isFile() && output.canWrite())) {
                final String msg = "Can not write to output file: " + output;
                getLogger().severe(msg);
                throw new IllegalArgumentException(msg);
            }

            getLogger().warning("Appending to file: " + output);
        }
        else
        {
            // Check for parent directory write rights
            File parentDir = output.getAbsoluteFile().getParentFile();
            if (!(parentDir.isDirectory() && parentDir.canWrite()))
            {
                final String msg = "Can not write to output directory: " + parentDir;
                getLogger().severe(msg);
                throw new IllegalArgumentException(msg);
            }
        }
    }

    @Override
    public void receive(EnrichedObservation enrichedObservation) {
        try (FileWriter fileWriter = new FileWriter(output, true)) {
            fileWriter.write(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                    enrichedObservation.getObservationTimestamp(),
                    enrichedObservation.getTicker(),
                    enrichedObservation.getPeriod(),
                    enrichedObservation.getMeasure(),
                    enrichedObservation.getMeasurement()));
            fileWriter.flush();
        } catch (IOException e) {
            getLogger().severe("Could not open: " + output);
        }
    }

    public File getOutputFile()
    {
        return output;
    }
}
