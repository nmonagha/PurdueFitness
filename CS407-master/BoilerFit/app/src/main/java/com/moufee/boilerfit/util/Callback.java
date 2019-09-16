package com.moufee.boilerfit.util;

import javax.annotation.Nullable;

public interface Callback<T> {
    void accept(@Nullable T t);
}
