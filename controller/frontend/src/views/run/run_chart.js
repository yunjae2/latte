import React from 'react';
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Brush, Label } from 'recharts';

export default function RunChart(props) {
    const { data } = props;

    const durationFormatter = (duration) => {
        /* TODO: Add case when test duration > 24h */
        if (duration >= 3600) {
            return new Date(duration * 1000).toISOString().substring(11, 19);
        } else {
            return new Date(duration * 1000).toISOString().substring(14, 19);
        }
    }

    return (
      <ResponsiveContainer width="100%" height="100%">
        <AreaChart
          data={data}
          margin={{
            top: 10,
            right: 45,
            left: 15,
            bottom: 20,
          }}
        >
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="duration" type="number" scale="time" domain={['auto', 'auto']} tickFormatter={durationFormatter}>
          </XAxis>
          <YAxis>
                <Label value="TPS" angle={-90} position="left" style={{ textAnchor: 'middle' }} />
          </YAxis>
          <Area type="linear" dataKey="tps" stroke="#8884d8" fill="#8884d8" isAnimationActive={false} />
          <Brush dataKey="duration" height={25} stroke="#8884d8" />
          <Tooltip formatter={(value, name, props) => [value, "TPS"]} labelFormatter={durationFormatter}/>
        </AreaChart>
      </ResponsiveContainer>
    );
}
