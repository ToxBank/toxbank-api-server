package org.toxbank.rest;

import java.io.File;
import java.io.FileFilter;
import java.util.Iterator;

public abstract class FilesIterator<T> implements Iterator<T> {

	protected File[] files;
	protected int index;
	
	public FilesIterator(File directory, FileFilter filter) {
		files = directory.listFiles(filter);
		index = -1;
	}
	@Override
	public boolean hasNext() {
		if (files == null) return false;
		index++;
		return (index >= 0) && (index<files.length); 
	}

	@Override
	public T next() {
		if (files == null) return null;
		return (index >= 0) && (index<files.length)?convert(files[index]):null;
	}

	protected abstract T convert(File file);
	@Override
	public void remove() {
	}
	
}