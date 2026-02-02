package com.scnsoft.exchange.adt.utils;

import com.google.common.base.Preconditions;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.io.BaseEncoding;
import com.google.common.io.ByteSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ByteArrayByteSource extends ByteSource {
        protected final byte[] bytes;

        public ByteArrayByteSource(byte[] bytes) {
            this.bytes = (byte[]) Preconditions.checkNotNull(bytes);
        }

        public InputStream openStream() {
            return new ByteArrayInputStream(this.bytes);
        }

        public InputStream openBufferedStream() throws IOException {
            return this.openStream();
        }

        public boolean isEmpty() {
            return this.bytes.length == 0;
        }

        public long size() {
            return (long)this.bytes.length;
        }

        public byte[] read() {
            return (byte[])this.bytes.clone();
        }

        public long copyTo(OutputStream output) throws IOException {
            output.write(this.bytes);
            return (long)this.bytes.length;
        }

        public HashCode hash(HashFunction hashFunction) throws IOException {
            return hashFunction.hashBytes(this.bytes);
        }

        public String toString() {
            return "ByteSource.wrap(" + BaseEncoding.base16().encode(this.bytes) + ")";
        }
    }