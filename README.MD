

# InjectEventBus
android 事件总线


##版本迭代
###1.0.0 beta

* 用注解重写EventBus(https://github.com/greenrobot/EventBus)

###1.0.1 

发现严重问题

项目暂停

###1.0.2 

问题解决



##如何集成到项目
直接从maven center compile

    compile 'com.diandi.klob:injecteventbus:1.0.2'

##如何使用

1.注册 EventBus 

    EventBus.getDefault().register(this);

2.注解 事件
    
    @Receiver(threadMode = ThreadMode.PostThread)
    public void myEvent(TestEvent event) {
        Toast.makeText(this, event.getMsg() + "", Toast.LENGTH_SHORT).show();
    }
3.定义 事件
 
    public final static class TestEvent implements Event {
        public String msg = "I am coming";

        public String getMsg() {
            return msg;
        }

    }
4.投递 事件
   
    EventBus.getDefault().post(new TestEvent());

5.反注册
   
    EventBus.getDefault().unregister(this);

##更多

请看      
     
             EventBus(https://github.com/greenrobot/EventBus)

             InjectEventBus(http://klob.diandi.life/?p=223)


***

#LICENSE
Copyright 2014-2015 klob(https://klob.diandi.life)

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.

You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.

See the License for the specific language governing permissions and limitations under the License.