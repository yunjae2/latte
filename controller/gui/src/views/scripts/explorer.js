import {CssBaseline, Paper, Typography} from "@mui/material";
import React from "react";
import { styled } from "@mui/material/styles";
import PropTypes from "prop-types";
import {TreeItem, TreeView } from "@mui/lab";
import { treeItemClasses } from "@mui/lab/TreeItem";
import FolderOutlinedIcon from '@mui/icons-material/FolderOutlined';
import InsertDriveFileIcon from '@mui/icons-material/InsertDriveFile';
import _ from "lodash";
import Box from "@mui/material/Box";
import Grid from "@mui/material/Grid";

const StyledTreeItemRoot = styled(TreeItem)(({ theme }) => ({
    color: theme.palette.text.secondary,
    [`& .${treeItemClasses.content}`]: {
        color: theme.palette.text.primary,
        paddingRight: theme.spacing(1),
        fontWeight: theme.typography.fontWeightMedium,
        '&.Mui-expanded': {
            fontWeight: theme.typography.fontWeightRegular,
        },
        '&:hover': {
            backgroundColor: theme.palette.action.hover,
        },
        '&.Mui-focused, &.Mui-selected, &.Mui-selected.Mui-focused': {
            backgroundColor: `var(--tree-view-bg-color, ${theme.palette.action.selected})`,
            color: 'var(--tree-view-color)',
        },
        [`& .${treeItemClasses.label}`]: {
            fontWeight: 'inherit',
            color: 'inherit',
        },
    }
}));

function StyledTreeItem(props) {
    const {
        bgColor,
        color,
        labelInfo,
        labelText,
        ...other
    } = props;

    return (
        <StyledTreeItemRoot
            label={
                <Box sx={{ display: 'flex', alignItems: 'center', p: 0.5, pr: 0 }}>
                    <Typography variant="body1" sx={{ fontWeight: 'inherit', flexGrow: 1 }}>
                        {labelText}
                    </Typography>
                    <Typography variant="caption" color="inherit">
                        {labelInfo}
                    </Typography>
                </Box>
            }
            style={{
                '--tree-view-color': color,
                '--tree-view-bg-color': bgColor,
            }}
            {...other}
        />
    );
}

StyledTreeItem.propTypes = {
    bgColor: PropTypes.string,
    color: PropTypes.string,
    labelInfo: PropTypes.string,
    labelText: PropTypes.string.isRequired,
};


export default function Explorer(props) {
    const { files, openFile } = props;

    const handleSelectFile = (file) => {
        openFile(file)
    }

    const buildFileTree = () => {
        let fileTree = {};
        files.forEach(file => {
            let dirname = file.key.split("/").slice(0,-1).join(".");
            let filename = file.key.split("/").at(-1);
            if (dirname === "") {
                fileTree[filename] = file;
            } else {
                if (!_.has(fileTree, dirname)) {
                    _.set(fileTree, dirname, {});
                }
                _.get(fileTree, dirname)[filename] = file;
            }
        })
        return fileTree;
    }

    const isFileNode = (node) => {
        return typeof Object.entries(node)[0][1] !== "object";
    }

    const relativeTime = (previous) => {
        let msPerMinute = 60 * 1000;
        let msPerHour = msPerMinute * 60;
        let msPerDay = msPerHour * 24;
        let msPerMonth = msPerDay * 30;
        let msPerYear = msPerDay * 365;

        let elapsed = Date.now() - previous;

        if (elapsed < msPerMinute) {
            return Math.floor(elapsed/1000) + ' seconds ago';
        }

        else if (elapsed < msPerHour) {
            return Math.floor(elapsed/msPerMinute) + ' minutes ago';
        }

        else if (elapsed < msPerDay ) {
            return Math.floor(elapsed/msPerHour ) + ' hours ago';
        }

        else if (elapsed < msPerMonth) {
            return Math.floor(elapsed/msPerDay) + ' days ago';
        }

        else if (elapsed < msPerYear) {
            return Math.floor(elapsed/msPerMonth) + ' months ago';
        }

        else {
            return Math.floor(elapsed/msPerYear ) + ' years ago';
        }
    }

    const buildTreeItems = (prefix, fileTree) => {
        if (_.isEmpty(fileTree)) {
            return null;
        }

        return Object.entries(fileTree)
            .sort(([key1, value1], [key2, value2]) => {
                if (isFileNode(value1) && !isFileNode(value2)) {
                    return 1;
                } else if (!isFileNode(value1) && isFileNode(value2)) {
                    return -1;
                } else {
                    // file or directory names
                    return key1 > key2 ? 1 : -1;
                }
            })
            .map(([key, value]) => {
                let newPrefix = prefix + "/" + key;
                if (isFileNode(value)) {
                    return <StyledTreeItem nodeId={newPrefix} labelText={key} labelInfo={relativeTime(value.modified)} onClick={() => { handleSelectFile(value) }} />;
                } else {
                    return (<StyledTreeItem nodeId={newPrefix} labelText={key} >
                        {buildTreeItems(newPrefix, value)}
                    </StyledTreeItem>);
                }
            })
    }

    return (
        <React.Fragment>
            <CssBaseline />
            <Paper elevation={2} sx={{ height: 1000, p: 1, borderRadius: 1, overflowY: "auto" }}>
                <Box sx={{ marginX: 1, marginBottom: 1, paddingBottom: 0.5, borderBottom: 1}}>
                    <Grid container>
                        <Grid item xs={6}>
                            <Typography variant="subtitle1" sx={{ fontWeight: "bold" }}>File</Typography>
                        </Grid>
                        <Grid item xs={6}>
                            <Typography textAlign="right" variant="subtitle1" sx={{ fontWeight: "bold" }}>Last modified</Typography>
                        </Grid>
                    </Grid>
                </Box>
                <TreeView
                    defaultParentIcon={<FolderOutlinedIcon />}
                    defaultEndIcon={<InsertDriveFileIcon />}
                >
                    { buildTreeItems("", buildFileTree()) }
                </TreeView>
            </Paper>
        </React.Fragment>
    );
}