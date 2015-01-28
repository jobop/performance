nohup java -DappHome="../conf" -jar ../lib/performance-1.0.0.SNAPSHOT.jar -l recode.log $@  >>performance.log  &
echo $!>pid

