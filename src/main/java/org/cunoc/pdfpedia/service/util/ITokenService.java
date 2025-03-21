package org.cunoc.pdfpedia.service.util;

import java.util.Collection;

public interface ITokenService {

    <T> String generateToken(long userId, Collection<T> authorities);
}
