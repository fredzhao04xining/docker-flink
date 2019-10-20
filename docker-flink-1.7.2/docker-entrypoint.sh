#!/bin/sh

if [ "$1" = "jobmanager" ]; then
    echo "Starting Job Manager"
    #sed -i -e "s/jobmanager.rpc.address: localhost/jobmanager.rpc.address: `hostname -f`/g" $FLINK_HOME/conf/flink-conf.yaml

    # make use of container linking and exploit the jobmanager entry in /etc/hosts
    sed -i -e "s/jobmanager.rpc.address: localhost/jobmanager.rpc.address: jobmanager/g" $FLINK_HOME/conf/flink-conf.yaml

    echo "config file: " && grep '^[^\n#]' $FLINK_HOME/conf/flink-conf.yaml
    $FLINK_HOME/bin/jobmanager.sh start cluster
elif [ "$1" = "taskmanager" ]; then

    # make use of container linking and exploit the jobmanager entry in /etc/hosts
    sed -i -e "s/jobmanager.rpc.address: localhost/jobmanager.rpc.address: jobmanager/g" $FLINK_HOME/conf/flink-conf.yaml

    sed -i -e "s/taskmanager.numberOfTaskSlots: 1/taskmanager.numberOfTaskSlots: `grep -c ^processor /proc/cpuinfo`/g" $FLINK_HOME/conf/flink-conf.yaml

    echo "Starting Task Manager"
    echo "config file: " && grep '^[^\n#]' $FLINK_HOME/conf/flink-conf.yaml
    $FLINK_HOME/bin/taskmanager.sh start
else
    $@
fi
