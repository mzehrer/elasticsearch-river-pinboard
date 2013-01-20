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

import org.elasticsearch.common.inject.AbstractModule;
import org.elasticsearch.river.River;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 19.01.13
 * Time: 15:21
 * To change this template use File | Settings | File Templates.
 */
public class PinboardRiverModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(River.class).to(PinboardRiver.class).asEagerSingleton();
    }

}
