import React from "react";
import _ from "lodash";
import { Box } from "@mui/system";
import { CartesianGrid, Legend, Line, ResponsiveContainer, LineChart, Tooltip, XAxis, YAxis, Label } from "recharts";

function LatencyLabel(props) {
    const { x, y, stroke, value } = props;

    return (
        <text x={x} y={y} dy={-10} fill={stroke} textAnchor="middle">
        </text>
    );
}

export default function LatencyChart(props) {
    const { rows } = props;
    const [maxYAxis, setMaxYAxis] = React.useState(0);

    const getStroke = num => {
        const strokes = [
            "#000000",
            "#E69F00",
            "#56B4E9",
            "#009E73",
            "#F0E442",
            "#0072B2",
            "#D55E00",
            "#CC79A7",
        ]

        if (num >= strokes.length) {
            return strokes[strokes.length - 1];
        }

        return strokes[num];
    }

    React.useEffect(() => {
        let maxValues = rows.map(row => row.latency)
            .map(latency => {
                let values = Object.keys(latency)
                    .filter(key => key.startsWith("p"))
                    .map(key => latency[key].toFixed(0));
                return Math.max(...values);
            });
        setMaxYAxis(Math.max(...maxValues));
    }, [rows]);

    const convertToChartData = (rows) => {
        let latencies = rows.map(row => row.latency)
            .map(latency => {
                return Object.keys(latency)
                    .filter(key => key.startsWith("p"))
                    .map(key => ({
                        percentile: key.replace("_", "."),
                        value: latency[key].toFixed(0),
                    }));
            });


        let converted = _.unzip(latencies).map(latency => {
            return latency.map(row => ({ ...row, value: [row.value] }))
                .reduce((prev, cur) => ({ ...prev, value: prev.value.concat(cur.value) }))
        });

        return converted;
    }

    const calcDomain = () => {
        return [0, maxYAxis];
    }

    return (
        <React.Fragment>
            <Box sx={{ margin: 1 }}>
                <ResponsiveContainer width="80%" height={300}>
                    <LineChart data={convertToChartData(rows)} margin={{ top: 5, right: 20, bottom: 5, left: 15 }}>
                        <Tooltip />
                        <XAxis dataKey="percentile">
                            <Label value="Percentile" position="insideBottom" offset={0} />
                        </XAxis>
                        <YAxis tick={{ dx: -10 }} domain={calcDomain()} >
                            <Label dx={-10} value="Latency (ms)" angle={-90} position="insideLeft" style={{ textAnchor: 'middle' }} />
                        </YAxis>
                        <CartesianGrid stroke="#ccc" strokeDasharray="5 5" />
                        <Legend layout="horizontal" verticalAlign="top" align="center" />
                        {_.range(rows.length).map(rowId => {
                            return <Line name={rows[rowId].name} dataKey={x => x.value[rowId]} stroke={getStroke(rowId)} label={<LatencyLabel />} />
                        })
                        }
                    </LineChart>
                </ResponsiveContainer>
            </Box>
        </React.Fragment>
    );
}