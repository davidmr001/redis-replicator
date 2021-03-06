/*
 * Copyright 2016-2018 Leon Chen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.moilioncircle.redis.replicator.cmd.parser;

import com.moilioncircle.redis.replicator.cmd.CommandParser;
import com.moilioncircle.redis.replicator.cmd.impl.MaxLen;
import com.moilioncircle.redis.replicator.cmd.impl.XAddCommand;
import com.moilioncircle.redis.replicator.util.ByteArrayMap;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toBytes;
import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toLong;
import static com.moilioncircle.redis.replicator.cmd.CommandParsers.toRune;
import static com.moilioncircle.redis.replicator.util.Strings.isEquals;

/**
 * @author Leon Chen
 * @since 2.6.0
 */
public class XAddParser implements CommandParser<XAddCommand> {
    @Override
    public XAddCommand parse(Object[] command) {
        int idx = 1;
        String key = toRune(command[idx]);
        byte[] rawKey = toBytes(command[idx]);
        idx++;
        MaxLen maxLen = null;
        if (isEquals(toRune(command[idx]), "MAXLEN")) {
            idx++;
            boolean approximation = false;
            if (Objects.equals(toRune(command[idx]), "~")) {
                approximation = true;
                idx++;
            }
            long count = toLong(command[idx]);
            idx++;
            maxLen = new MaxLen(approximation, count);
        }
        String id = toRune(command[idx]);
        byte[] rawId = toBytes(command[idx]);
        idx++;
        Map<String, String> fields = new LinkedHashMap<>();
        ByteArrayMap<byte[]> rawFields = new ByteArrayMap<>();
        while (idx < command.length) {
            String field = toRune(command[idx]);
            byte[] rawField = toBytes(command[idx]);
            idx++;
            String value = idx == command.length ? null : toRune(command[idx]);
            byte[] rawValue = idx == command.length ? null : toBytes(command[idx]);
            idx++;
            fields.put(field, value);
            rawFields.put(rawField, rawValue);
        }
        return new XAddCommand(key, maxLen, id, fields, rawKey, rawId, rawFields);
    }
}
