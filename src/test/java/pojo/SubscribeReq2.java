/*
 * Copyright 2013-2018 Lilinfeng.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pojo;

import io.netty.handler.codec.socksx.v5.DefaultSocks5InitialRequest;
import io.netty.handler.codec.socksx.v5.Socks5AuthMethod;

import java.io.Serializable;

/**
 * @author Lilinfeng
 * @date 2014年2月23日
 * @version 1.0
 */
public class SubscribeReq2   extends DefaultSocks5InitialRequest implements Serializable{
    public SubscribeReq2(Socks5AuthMethod... authMethods) {
        super(authMethods);
    }

    public SubscribeReq2(Iterable<Socks5AuthMethod> authMethods) {
        super(authMethods);
    }
}
