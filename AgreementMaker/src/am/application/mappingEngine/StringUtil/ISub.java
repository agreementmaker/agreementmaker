package am.application.mappingEngine.StringUtil;



/**
 * @author G. Stoilos & G. Stamou & S. Kollias
 * From Falcon-AO
 * 
 */
public class ISub
{
    public static double getSimilarity(String s, String t)
    {
        int sl = s.length(), tl = t.length();
        if (sl <= 2 || tl <= 2) {
			return  getEditSimilarity(s, t); //the edit distance class of falconAO is inside this class
        } else {
            return score(s, t);
        }
    }

    public static double score(String st1, String st2)
    {
        if (st1 == null || st2 == null || st1.length() == 0 || st2.length() == 0) {
            return 0;
        }
        String s1 = st1.toLowerCase();
        String s2 = st2.toLowerCase();
        s1 = normalizeString(s1, '.');
        s2 = normalizeString(s2, '.');
        s1 = normalizeString(s1, '_');
        s2 = normalizeString(s2, '_');
        s1 = normalizeString(s1, ' ');
        s2 = normalizeString(s2, ' ');
        int l1 = s1.length(), l2 = s2.length();
        int L1 = l1, L2 = l2;
        if ((L1 == 0) && (L2 == 0)) {
            return 0;
        }
        if ((L1 == 0) || (L2 == 0)) {
            return 1;
        }
        double common = 0;
        int best = 2;
        while (s1.length() > 0 && s2.length() > 0 && best != 0) {
            best = 0;
            l1 = s1.length();
            l2 = s2.length();
            int i = 0, j = 0;
            int startS1 = 0, endS1 = 0;
            int startS2 = 0, endS2 = 0;
            int p = 0;
            for (i = 0; (i < l1) && (l1 - i > best); i++) {
                j = 0;
                while (l2 - j > best) {
                    int k = i;
                    for (; (j < l2) && (s1.charAt(k) != s2.charAt(j)); j++) {
                    }
                    if (j != l2) {
                        p = j;
                        for (j++, k ++; (j < l2) && (k < l1)
                                && (s1.charAt(k) == s2.charAt(j)); j++, k++) {
			}
			if (k - i > best) {
                            best = k - i;
                            startS1 = i;
                            endS1 = k;
                            startS2 = p;
                            endS2 = j;
			}
                    }
		}
            }
            char[] newString = new char[s1.length() - (endS1 - startS1)];
            j = 0;
            for (i = 0; i < s1.length(); i++) {
                if (i >= startS1 && i < endS1) {
                    continue;
                }
                newString[j++] = s1.charAt(i);
            }
            s1 = new String(newString);
            newString = new char[s2.length() - (endS2 - startS2)];
            j = 0;
            for (i = 0; i < s2.length(); i++) {
                if (i >= startS2 && i < endS2) {
                    continue;
                }
                newString[j++] = s2.charAt(i);
            }
            s2 = new String(newString);
            if (best > 2) {
                common += best;
            } else {
                best = 0;
            }
        }
        double commonality = 0;
        double scaledCommon = (double) (2 * common) / (L1 + L2);
        commonality = scaledCommon;
        double winklerImprovement = winklerImprovement(st1, st2, commonality);
        double dissimilarity = 0;
        double rest1 = L1 - common;
        double rest2 = L2 - common;
        double unmatchedS1 = Math.max(rest1, 0);
        double unmatchedS2 = Math.max(rest2, 0);
        unmatchedS1 = rest1 / L1;
        unmatchedS2 = rest2 / L2;
        double suma = unmatchedS1 + unmatchedS2;
        double product = unmatchedS1 * unmatchedS2;
        double p = 0.6;
        if ((suma - product) == 0) {
            dissimilarity = 0;
        } else {
            dissimilarity = (product) / (p + (1 - p) * (suma - product));
        }
        
        double result = commonality - dissimilarity + winklerImprovement; 
        if(result < 0)
        	result = 0;
        return result;
    }

    private static double winklerImprovement(String s1, String s2,
            double commonality)
    {
        int i, n = Math.min(s1.length(), s2.length());
        for (i = 0; i < n; i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                break;
            }
        }
        double commonPrefixLength = Math.min(4, i);
        double winkler = commonPrefixLength * 0.1 * (1 - commonality);
        return winkler;
    }

    private static String normalizeString(String str, char remo)
    {
        StringBuffer strBuf = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) != remo) {
                strBuf.append(str.charAt(i));
            }
        }
        return strBuf.toString();
    }
    
    public static double getEditSimilarity(String s, String t)
    {
        int edit = getEditDistance(s, t);
        double sim = 1 / Math.exp(edit / (double) (s.length() + t.length() - edit));
        return sim;
    }
    
    public static int getEditDistance(String s, String t)
    {
        int d[][];
        int n;
        int m;
        int i;
        int j;
        char s_i;
        char t_j;
        int cost;

        n = s.length();
        m = t.length();
        if (n == 0) {
            return m;
        }
        if (m == 0) {
            return n;
        }
        d = new int[n + 1][m + 1];
        for (i = 0; i <= n; i++) {
            d[i][0] = i;

        }
        for (j = 0; j <= m; j++) {
            d[0][j] = j;

        }
        for (i = 1; i <= n; i++) {
            s_i = s.charAt(i - 1);
            for (j = 1; j <= m; j++) {
                t_j = t.charAt(j - 1);
                if (s_i == t_j) {
                    cost = 0;
                } else {
                    cost = 1;
                }
                d[i][j] = Minimum(d[i - 1][j] + 1, d[i][j - 1] + 1,
                        d[i - 1][j - 1] + cost);
            }
        }
        return d[n][m];

    }

    private static int Minimum(int a, int b, int c)
    {
        int mi;
        mi = a;
        if (b < mi) {
            mi = b;
        }
        if (c < mi) {
            mi = c;
        }
        return mi;
    }


}
