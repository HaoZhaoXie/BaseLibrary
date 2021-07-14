package com.cangjiedata.baselibrary.bean;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

/**
 * 资源结果包装类，此类反应资源获取的状态和结果
 * @param <T>
 */
public class Resource<T> {
    @NonNull
    public final Status status;

    public final int code;

    @Nullable
    public final T data;

    public Resource(@NonNull Status status, @Nullable T data, @Nullable int code) {
        this.status = status;
        this.data = data;
        this.code = code;
    }

    public static <T> Resource<T> success(@Nullable T data) {
        return new Resource<>(Status.SUCCESS, data, 0);
    }

    public static <T> Resource<T> error(int code, @Nullable T data) {
        return new Resource<>(Status.ERROR, data, code);
    }

    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(Status.LOADING, data, 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Resource<?> resource = (Resource<?>) o;

        if (status != resource.status) {
            return false;
        }
        return Objects.equals(data, resource.data);
    }

    @Override
    public int hashCode() {
        int result = status.hashCode();
        result = 31 * result;
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Resource{" +
                "status=" + status +
                ", data=" + data +
                '}';
    }
}
