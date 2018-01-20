package com.vvnc.lorem;

public class LoremIpsumGenerator {
    private static final String[] loremIpsumText;
    private static final char separator = ' ';
    private static int currentWord;

    static {
        currentWord = 0;
        loremIpsumText = new String[]{
                "lorem",
                "ipsum",
                "dolor",
                "sit",
                "amet",
                "consectetur",
                "adipiscing",
                "elit",
                "sed",
                "do",
                "eiusmod",
                "tempor",
                "incididunt",
                "ut",
                "labore",
                "et",
                "dolore",
                "magna",
                "aliqua",
                "ut",
                "enim",
                "ad",
                "minim",
                "veniam",
                "quis",
                "nostrud",
                "exercitation",
                "ullamco",
                "laboris",
                "nisi",
                "ut",
                "aliquip",
                "ex",
                "ea",
                "commodo",
                "consequat",
                "duis",
                "aute",
                "irure",
                "dolor",
                "in",
                "reprehenderit",
                "in",
                "voluptate",
                "velit",
                "esse",
                "cillum",
                "dolore",
                "eu",
                "fugiat",
                "nulla",
                "pariatur",
                "excepteur",
                "sint",
                "occaecat",
                "cupidatat",
                "non",
                "proident",
                "sunt",
                "in",
                "culpa",
                "qui",
                "officia",
                "deserunt",
                "mollit",
                "anim",
                "id",
                "est",
                "laborum"
        };
    }

    public static void reset() {
        currentWord = 0;
    }

    public static String getNext(int wordCount, Capitalization capitalization)
            throws LoremException {
        // Self test:
        if (currentWord < 0) {
            currentWord = 0;
        }
        StringBuilder sb = new StringBuilder();
        boolean isFirstWord = true;
        while (wordCount-- > 0) {
            if (currentWord >= loremIpsumText.length) {
                // If out of bounds then start over from the beginning:
                currentWord = 0;
            }
            String word = loremIpsumText[currentWord++];
            if (capitalization == Capitalization.NONE) {
                sb.append(word);
            } else if (capitalization == Capitalization.UPPERCASE) {
                sb.append(word.toUpperCase());
            } else if (capitalization == Capitalization.EACH_WORD) {
                if (word.length() > 0) {
                    sb.append(Character.toUpperCase(word.charAt(0)));
                    if (word.length() > 1) {
                        sb.append(word.substring(1));
                    }
                }
            } else if (capitalization == Capitalization.FIRST_WORD) {
                if (isFirstWord) {
                    isFirstWord = false;
                    if (word.length() > 0) {
                        sb.append(Character.toUpperCase(word.charAt(0)));
                        if (word.length() > 1) {
                            sb.append(word.substring(1));
                        }
                    }
                } else {
                    sb.append(word);
                }
            } else {
                throw new LoremException(
                        String.format("Unknown capitalization mode: %s", capitalization));
            }

            if (wordCount != 0) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }
}
