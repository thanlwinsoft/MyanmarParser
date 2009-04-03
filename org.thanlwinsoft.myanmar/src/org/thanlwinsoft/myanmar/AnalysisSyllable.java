package org.thanlwinsoft.myanmar;

import java.util.ArrayDeque;
import java.util.Iterator;

/**
 * Class to hold syllable information
 * @author keith
 *
 */
public class AnalysisSyllable implements Comparable<AnalysisSyllable>
{
        private String mText;
        private ArrayDeque<String> mFollowing;
        /**
         * Syllable constructor
         * @param s - the syllable
         * @param following syllables
         */
        public AnalysisSyllable(String s, ArrayDeque<String> following)
        {
            mText = s;
            mFollowing = following;
        }

        @Override
        public int compareTo(AnalysisSyllable o)
        {
            int r = mText.compareTo(o.mText);
            Iterator<String> i = mFollowing.iterator();
            Iterator<String> j = o.mFollowing.iterator();
            while (r == 0)
            {
                if (i.hasNext())
                    if (j.hasNext())
                    {
                        r = i.next().compareTo(j.next());
                    }
                    else
                    {
                        r = 1;
                        break;
                    }
                else if (j.hasNext())
                {
                    r = -1;
                    break;
                }
                else 
                {
                    r = 0;
                    break;
                }
            }
            return r;
        }
        

        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            sb.append(mText);
            Iterator <String> i = mFollowing.iterator();
            while (i.hasNext())
                sb.append(i.next());
            return sb.toString();
        }
        

}
