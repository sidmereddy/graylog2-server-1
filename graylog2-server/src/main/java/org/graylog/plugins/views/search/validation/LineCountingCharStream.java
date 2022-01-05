/*
 * Copyright (C) 2020 Graylog, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * Server Side Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program. If not, see
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package org.graylog.plugins.views.search.validation;

import org.apache.lucene.queryparser.classic.CharStream;

import java.io.IOException;

public class LineCountingCharStream implements CharStream {

    private final CharStream delegate;
    private int lineBegin = 0;
    private int lineCounter = 0;
    private int thisLineStartPosition = 0;

    public LineCountingCharStream(CharStream delegate) {
        this.delegate = delegate;
    }

    @Override
    public char readChar() throws IOException {
        final char oneChar = delegate.readChar();
        if (oneChar == '\n') {
            thisLineStartPosition = getEndColumn();
            lineCounter++;
        }
        return oneChar;
    }

    @Override
    public int getColumn() {
        return delegate.getColumn();
    }

    @Override
    public int getLine() {
        return delegate.getLine();
    }

    @Override
    public int getEndColumn() {
        if (lineCounter < 1) {
            return delegate.getEndColumn();
        } else {
            return delegate.getEndColumn() - thisLineStartPosition;
        }
    }

    @Override
    public int getEndLine() {
        return 1 + lineCounter;
    }

    @Override
    public int getBeginColumn() {
        if (lineCounter < 1) {
            return delegate.getBeginColumn();
        } else {
            return delegate.getBeginColumn() - thisLineStartPosition;
        }
    }

    @Override
    public int getBeginLine() {
        return 1 + lineBegin;
    }

    @Override
    public void backup(int amount) {
        delegate.backup(amount);
    }

    @Override
    public char BeginToken() throws IOException {
        lineBegin = lineCounter;
        return delegate.BeginToken();
    }

    @Override
    public String GetImage() {
        return delegate.GetImage();
    }

    @Override
    public char[] GetSuffix(int len) {
        return delegate.GetSuffix(len);
    }

    @Override
    public void Done() {
        delegate.Done();
    }
}
