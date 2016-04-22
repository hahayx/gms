package com.hh.common.data;

public interface Builder<E, T> {
	public T build(E e) throws Exception;
}
