package com.todoary.ms.util;

import com.ibm.icu.text.BreakIterator;
import lombok.Getter;

@Getter
public enum FormatInfo {
    CATEGORY_TITLE_LENGTH(5),
    DIARY_TITLE_LENGTH(20),
    TODO_TITLE_LENGTH(20);

    private final int length;

    private FormatInfo(int length) {
        this.length = length;
    }

    /**
     * 사용자가 인식하는 글자 단위로 개수 세기
     * 참고
     * - 글자 수를 세는 7가지 방법: https://engineering.linecorp.com/ko/blog/the-7-ways-of-counting-characters/
     * - 자바 기본 라이브러리 사용 시 몇몇 이모티콘을 제대로 세지 못하는 문제: https://stackoverflow.com/questions/40878804/how-to-count-grapheme-clusters-or-perceived-emoji-characters-in-java
     *
     * @param value
     * @return count
     */
    public static int getGraphemeLength(String value) {
        BreakIterator it = BreakIterator.getCharacterInstance();
        it.setText(value);
        int count = 0;
        while (it.next() != BreakIterator.DONE) {
            count++;
        }
        return count;
    }
}
