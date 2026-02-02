package com.scnsoft.eldermark.entity;

public enum NameUseCode {
    C(0),       /*License or other document name that differs from legal name*/
    I(1),       /*Indigenous/Tribal*/
    L(2),       /*Legal name*/
    P(3),       /*Pseudonym – another name that is not the legal name and not the primary name by which the person is called*/
    A(4),       /*Artist/stage name*/
    R(5),       /*Religious name*/
    SRCH(6),    /*Name used for searching*/
    PHON(7),    /*Phonetic spelling of name*/
    SNDX(8),    /*A “soundex code” for the name*/
    ABC(9),     /*Alphabetic transcription of name (e.g. Japanese Romanji)*/
    SYL(10),    /*Syllabic script transcription (e.g. Kana or Hangul)*/
    IDE(11);    /*Ideographic representation of name (e.g. Kanji)*/

    private final int code;

    NameUseCode(final int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
