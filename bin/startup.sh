nohup java -DappHome="../conf" -jar ../lib/performance-${project.version}.jar -l recode.log $@  >>performance.log  &
echo $!>pid

