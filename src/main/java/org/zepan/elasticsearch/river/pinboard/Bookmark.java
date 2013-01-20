/*
 * Copyright (C) 2013 Michael Zehrer <zehrer@zepan.org>
 *
 * This file is part of pinboard-river.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.zepan.elasticsearch.river.pinboard;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 19.01.13
 * Time: 16:20
 * To change this template use File | Settings | File Templates.
 */
public class Bookmark {

    private String href;
    private String description;
    private String extended;
    private String meta;
    private String hash;
    private Date time;
    private boolean shared;
    private boolean toread;
    private String tags;

    public Bookmark() {
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExtended() {
        return extended;
    }

    public void setExtended(String extended) {
        this.extended = extended;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public boolean isToread() {
        return toread;
    }

    public void setToread(boolean toread) {
        this.toread = toread;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
