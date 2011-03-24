RINGS = 13; %number of measured distances per sensor
QUANTITY = 50; %number of trials per distance
SENSORS = 8; %number of sensors

load 'e:\Programme\eclipse-SDK-3.0.1-win32 Read\eclipse\workspace\org.micropsi.whiskerbot\Calibration_statistics_data.txt'  
load 'e:\Programme\eclipse-SDK-3.0.1-win32 Read\eclipse\workspace\org.micropsi.whiskerbot\Calibration_raw_data.txt'
data =  Calibration_raw_data;
statistics = Calibration_statistics_data;

[rows_data, columns_data] = size(data);
cm = ones(rows_data, columns_data);

for(i=1:RINGS)
    cm(1:rows_data,(i-1)*QUANTITY+1:i*QUANTITY) = i;
end
figure(2), subplot(2,2,1), plot(cm',data');

figure(3);
s = statistics;
for (i=1:SENSORS)
    h = subplot(4,2,i);
    errorbar((1:RINGS)',s(((i-1)*RINGS)+1:i*RINGS,3), s(((i-1)*RINGS)+1:i*RINGS,3)...
        - s(((i-1)*RINGS)+1:i*RINGS,2),s(((i-1)*RINGS)+1:i*RINGS,1) - s(((i-1)*RINGS)+1:i*RINGS,3))
    xlabel('distance to wall [cm]');
    ylabel('activation')
    title(['\bf proximity sensor ',int2str(i),' ']);
    XLim([0.5 13.5]), YLim([0 1100]);
    set(h,'XTick',[1:13]);
end

figure(4);
for (i=1:SENSORS)
    [p,S] = polyfit((1:RINGS)',s(((i-1)*RINGS)+1:i*RINGS,3),6);
    h = subplot(4,2,i);
    %plot((1:RINGS)',s(((i-1)*RINGS)+1:i*RINGS,3),'o',(1:RINGS)',polyval(p,(1:RINGS)),'-');
    plot((1:RINGS)',s(((i-1)*RINGS)+1:i*RINGS,3),'o',(1:0.1:RINGS)',polyval(p,(1:0.1:RINGS)),'-');
    
end

