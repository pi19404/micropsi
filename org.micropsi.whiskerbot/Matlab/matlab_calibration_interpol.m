RINGS = 13; %number of measured distances per sensor
QUANTITY = 50; %number of trials per distance
SENSORS = 8; %number of sensors

load 'C:\eclipse-SDK-3.0.1-win32-READ\eclipse\workspace\org.micropsi.whiskerbot\Matlab\Calibration_statistics_data.txt'  
load 'C:\eclipse-SDK-3.0.1-win32-READ\eclipse\workspace\org.micropsi.whiskerbot\Matlab\Calibration_raw_data.txt'
data =  Calibration_raw_data;
statistics = Calibration_statistics_data;

% p_mean = 0;
% p_sum = 0;
% s_mean = zeros(RINGS,1);
% s_sum = zeros(RINGS,1);



figure(10);
s = statistics;
for (i=1:SENSORS)
    h1 = subplot(4,2,i);
    errorbar((1:RINGS)',s(((i-1)*RINGS)+1:i*RINGS,3), s(((i-1)*RINGS)+1:i*RINGS,3)...
        - s(((i-1)*RINGS)+1:i*RINGS,2),s(((i-1)*RINGS)+1:i*RINGS,1) - s(((i-1)*RINGS)+1:i*RINGS,3))
    xlabel('distance to wall [cm]');
    ylabel('activation')
    title(['\bf proximity sensor ',int2str(i),' ']);
    XLim([0.5 13.5]), YLim([0 1100]);
    set(h1,'XTick',[1:13]);
end


for (i=1:SENSORS)
    %============================================
    %sensordata = (0:1030)';
    sensordata = s(((i-1)*RINGS)+1:i*RINGS,3); %3rd column contains mean values
    sensor_range = (sensordata(1):-1:sensordata(length(sensordata)))';
    %sensordata_ext = [max(s(((i-1)*RINGS)+1:i*RINGS,1)); sensordata; min(s(((i-1)*RINGS)+1:i*RINGS,2))] %1st column contains max values
                                                                                                         %2nd column contains min values      
  
    sensordata_ext = [1023; sensordata; min(s(((i-1)*RINGS)+1:i*RINGS,2))] %1st column contains max values
                                                                           %2nd column contains min values      
                                                                                                         
    sensor_range_ext = (sensordata_ext(1):-1:sensordata_ext(length(sensordata_ext)))';
    
    cm_range = (1:RINGS)';
    cm_range_ext = [(0:RINGS),20]';
    
    %%%%s_mean2 = [sensor_range(length(sensor_range)), s_mean', sensor_range(1)]';
    
    %s_mean2_extrap = interp1(s_mean2,cm_range,sensordata); 
    s_mean2_extrap = interp1(sensordata_ext, cm_range_ext, sensor_range_ext);
    
   
 i   
[int32(sensordata_ext), int32(cm_range_ext)]

figure(2);
subplot(4,2,i);
    plot( sensor_range_ext ,s_mean2_extrap,'r',sensordata_ext, cm_range_ext,'o');
    ylabel('distance to wall [cm]');
    xlabel('activation');
    title(['\bf proximity sensor ',int2str(i),' + interpolation fit']);

file = ['sensor_prox',int2str(i),'_extrapol_sensordata.txt']     
save(file, 'sensor_range_ext', '-ASCII', '-DOUBLE');
file = ['sensor_prox',int2str(i),'_extrapol_distance.txt']     
save(file, 's_mean2_extrap', '-ASCII', '-DOUBLE');

%save sensor_prox_extrapol_distance.txt s_mean2_extrap -ASCII -DOUBLE

end
%save sensor_prox(40to1050)_extrapol_cm(14to0)__sensordata.txt sensordata -ASCII -DOUBLE
%save sensor_prox(40to1050)_extrapol_cm(0to14)__distance.txt s_mean2_extrap -ASCII -DOUBLE
    
    %============================================
%     
%     [p,S] = polyfit((1:RINGS)',s(((i-1)*RINGS)+1:i*RINGS,3),6);
%     h2 = subplot(6,2,i);
%     %plot((1:RINGS)',s(((i-1)*RINGS)+1:i*RINGS,3),'o',(1:RINGS)',polyval(p,(1:RINGS)),'-');
%     plot((1:RINGS)',s(((i-1)*RINGS)+1:i*RINGS,3),'o',(1:0.1:RINGS)',polyval(p,(1:0.1:RINGS)),'-');
%     xlabel('distance to wall [cm]');
%     ylabel('activation')
%     title(['\bf proximity sensor ',int2str(i),' + polynomial fit']);
%     XLim([0.5 13.5]), YLim([0 1100]);
%     set(h2,'XTick',[1:13]);
%     
%     p_sum = p_sum + p;
%     s_sum = s_sum + s(((i-1)*RINGS)+1:i*RINGS,3)
% end
% 
% p_mean = p_sum / SENSORS
% s_mean = s_sum / SENSORS
% 
% %get(h2);
% figure(4); 
% subplot(6,2,(9:12));
%     plot((1:RINGS)',s_mean,'o',(1:0.1:RINGS)',polyval(p_mean,(1:0.1:RINGS)),'-');
%     xlabel('distance to wall [cm]');
%     ylabel('activation')
%     title(['\bf mean proximity sensors 1-8 + polynomial fit']);
%     XLim([0.5 13.5]), YLim([0 1100]);
%     set(h2,'XTick',[1:13]);
%     
%  save p_mean.txt p_mean -ASCII -DOUBLE
