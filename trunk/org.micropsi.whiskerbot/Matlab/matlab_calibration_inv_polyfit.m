RINGS = 13; %number of measured distances per sensor
QUANTITY = 50; %number of trials per distance
SENSORS = 8; %number of sensors

load 'C:\eclipse-SDK-3.0.1-win32-READ\eclipse\workspace\org.micropsi.whiskerbot\matlab\Calibration_statistics_data.txt'  
load 'C:\eclipse-SDK-3.0.1-win32-READ\eclipse\workspace\org.micropsi.whiskerbot\matlab\Calibration_raw_data.txt'
data =  Calibration_raw_data;
statistics = Calibration_statistics_data;

p_mean = 0;
p_sum = 0;
s_mean = zeros(RINGS,1);
s_sum = zeros(RINGS,1);


[rows_data, columns_data] = size(data);
cm = ones(rows_data, columns_data);

for(i=1:RINGS)
    cm(1:rows_data,(i-1)*QUANTITY+1:i*QUANTITY) = i;
end
figure(2), subplot(2,2,1), plot(cm',data');

figure(3);
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

figure(4);
for (i=1:SENSORS)
    [p,S] = polyfit(s(((i-1)*RINGS)+1:i*RINGS,3),(1:RINGS)',6);
    h2 = subplot(6,2,i);
    %plot(s(((i-1)*RINGS)+1:i*RINGS,3),(1:RINGS)','o',polyval(p,(1:0.1:RINGS)),(1:0.1:RINGS)','-');
    plot(s(((i-1)*RINGS)+1:i*RINGS,3),(1:RINGS)','o',polyval(p,(1:0.1:RINGS)),(1:0.1:RINGS)','-');
    xlabel('distance to wall [cm]');
    ylabel('activation')
    title(['\bf proximity sensor ',int2str(i),' + polynomial fit']);
    XLim([0.5 13.5]), YLim([0 1100]);
    set(h2,'XTick',[1:13]);
    
    p_sum = p_sum + p;
    s_sum = s_sum + s(((i-1)*RINGS)+1:i*RINGS,3)
end

p_mean = p_sum / SENSORS
s_mean = s_sum / SENSORS
%save p_mean.txt p_mean -ASCII -DOUBLE
save s_mean.txt s_mean -ASCII -DOUBLE

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
