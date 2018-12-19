#!/bin/bash

#杀掉进程
appName=72cun
pid=$(pgrep -f ${appName} )
num=0
for buff in ${pid}
do
   num=`expr ${num} + 1`
done

if [ ${num} == 2 ]
  then 
     kill -9 $(pgrep -f ${appName} | sed -n '1p')
     echo app stopped
fi

#启动应用
path=/72cun
files=$(ls $path)
for filename in $files
do
 app=$(expr index "$filename " 72cun)
  if [ $app == 1 ]
 then 
   nohup java -jar /72cun/$filename --spring.profiles.active=prod --server.port=9999 >  /72cun/temp.log &
   break
 fi
done

#打印日志
tail -f /72cun/temp.log