package com.scnsoft.eldermark.service.transformer;

public interface BiConverter <S,T,R> {

    R convert(S s, T t);

}
