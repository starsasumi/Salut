package com.starbugs.salut.test.libjitsi;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by starsasumi on 08/04/2014.
 */
public class CommandLineParser {

    static Map<String, String> parseCommandLineArgs(String[] args)
    {
        Map<String, String> argMap = new HashMap<String, String>();

        for (String arg : args)
        {
            int keyEndIndex = arg.indexOf('=');
            String key;
            String value;

            if (keyEndIndex == -1)
            {
                key = arg;
                value = null;
            }
            else
            {
                key = arg.substring(0, keyEndIndex + 1);
                value = arg.substring(keyEndIndex + 1);
            }
            argMap.put(key, value);
        }
        return argMap;
    }
}
