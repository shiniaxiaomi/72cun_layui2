#!/bin/bash

#杀掉进程
kill -9 `cat /72cun/72cun.pid`

#启动应用
path=/72cun
files=$(ls $path)
for filename in $files
do
 if [[ $filename == *.jar* ]]
  then
     nohup java -jar /72cun/$filename --spring.profiles.active=prod --server.port=9999 > /72cun/temp.log & echo $! > /72cun/72cun.pid
     break
 fi
done

#打印日志
tail -f /72cun/temp.log