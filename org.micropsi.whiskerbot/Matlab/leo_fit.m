load 'C:\eclipse-SDK-3.0.1-win32-READ\eclipse\workspace\org.micropsi.whiskerbot\matlab\Calibration_statistics_data.txt'  
load 'C:\eclipse-SDK-3.0.1-win32-READ\eclipse\workspace\org.micropsi.whiskerbot\matlab\Calibration_raw_data.txt'
load 'C:\eclipse-SDK-3.0.1-win32-READ\eclipse\workspace\org.micropsi.whiskerbot\matlab\s_mean.txt'

RINGS=13;
i=3

data =  Calibration_raw_data;
stat = Calibration_statistics_data;
s_mean = s_mean;
s = stat(((i-1)*RINGS)+1:i*RINGS,3)
s_continuous = min(s):max(s);

%%s_mean_continuous:
smc = min(s_mean):max(s_mean); 


[p1 S1]=polyfit(s,(1:RINGS)',3)
%plot(polyval(p1,s(((i-1)*RINGS)+1:i*RINGS,3)),(1:RINGS)','-');
figure(6)
subplot(3,1,1)
    plot(s,(1:RINGS)','o',s_continuous,polyval(p1,s_continuous),'r-')

[p2 S2]=polyfit(s_mean,(1:RINGS)',3)
subplot(3,1,2)    
    plot(s_mean,(1:RINGS)','o',smc,polyval(p2,smc),'r-')

sm_interp = interp1(s_mean,(1:RINGS)',smc);    
subplot(3,1,3)    
    %plot(1:((RINGS-1)/length(s_continuous)):RINGS-((RINGS-1)/length(s_continuous))',polyval(p1,s_continuous),'r-');
    plot(s,(1:RINGS)','o',smc ,sm_interp,'r-');

%s_wide_interp = interp1(s_mean,(1:RINGS)',40:1200);    
%%=========================================================================
%%=now with interpolation instead of polynomial fit:
%%=========================================================================
%%new start and end values:
%sensordata = (70:1100)';
%sensordata = (40:1050)';
sensordata = (0:1030)';
cm_range = [(0:RINGS),20]';
s_mean2 = [sensordata(length(sensordata)), s_mean', sensordata(1)]';

s_mean2(2)=1000; %choose 1000 instead of 1020 as 0cm!
cm_range(2)=0;

s_mean2_extrap = interp1(s_mean2,cm_range,sensordata);    

[int32(s_mean2), int32(cm_range)]

figure(7);
h = plot( sensordata ,s_mean2_extrap,'r',s_mean2, cm_range,'o');
    ylabel('distance to wall [cm]');
    xlabel('activation');
    title(['\bf proximity sensors (mean) + interpolation fit']);

save sensor_prox(40to1050)_extrapol_cm(14to0)__sensordata.txt sensordata -ASCII -DOUBLE
save sensor_prox(40to1050)_extrapol_cm(0to14)__distance.txt s_mean2_extrap -ASCII -DOUBLE

% [p1 S1]=polyfit(s(((i-1)*RINGS)+1:i*RINGS,3),(1:RINGS)',3)
% %plot(polyval(p1,s(((i-1)*RINGS)+1:i*RINGS,3)),(1:RINGS)','-');
% figure(6)
% subplot(2,1,1)
%     plot(s(((i-1)*RINGS)+1:i*RINGS,3),(1:RINGS)','o')
% subplot(2,1,2)    
%     plot(polyval(p1,s(((i-1)*RINGS)+1:i*RINGS,3)),(1:RINGS)','-');