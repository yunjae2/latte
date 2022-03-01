import React, { useEffect, useState } from 'react'
import Moment from 'moment'
import 'font-awesome/css/font-awesome.min.css'
import {Container, CssBaseline, TextField} from '@mui/material'
import ScriptEditor from './editor'
import { Box } from '@mui/system'
import Explorer from "./explorer";

const defaultFiles = [
    {
        key: 'photos/animals/cat in a hat.png',
        modified: +Moment().subtract(1, 'hours'),
        size: 1.5 * 1024 * 1024,
    },
    {
        key: 'photos/animals/kitten_ball.png',
        modified: +Moment().subtract(3, 'days'),
        size: 545 * 1024,
    },
    {
        key: 'photos/animals/elephants.png',
        modified: +Moment().subtract(3, 'days'),
        size: 52 * 1024,
    },
    {
        key: 'photos/funny fall.gif',
        modified: +Moment().subtract(2, 'months'),
        size: 13.2 * 1024 * 1024,
    },
    {
        key: 'photos/holiday.jpg',
        modified: +Moment().subtract(25, 'days'),
        size: 85 * 1024,
    },
    {
        key: 'documents/letter chunks.doc',
        modified: +Moment().subtract(15, 'days'),
        size: 480 * 1024,
        content: "<h1> hello world!! </h1>"
    },
    {
        key: 'documents/export.pdf',
        modified: +Moment().subtract(15, 'days'),
        size: 4.2 * 1024 * 1024,
    },
];

export default function Scripts() {
    const [files, setFiles] = useState([]);
    const [editorOpen, setEditorOpen] = useState(false);
    const [openFileName, setOpenFileName] = useState(null);
    const [openFileContent, setOpenFileContent] = useState(null);

    const getRepositoryUrl = () => {
        return "http://" + window.location.hostname + ":8082/latte_repo";
    }

    const handleCommitFile = (fileName, content, message) => {
        fetch("/api/script/commit", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                fileName,
                content,
                message,
            }),
        })
            .then(res => {
                if (!res.ok) {
                    throw new Error();
                }
            })
            .then(() => loadFiles())
            .then(() => setEditorOpen(false))
            .then(() => setOpenFileName(null))
            .then(() => setOpenFileContent(null))
            .catch(error => alert("Commit failed"));
    }

    const loadFiles = () => {
        fetch("/api/script/all")
            .then(res => res.json())
            .then(res => res.fileInfos)
            .then(fetchedFiles => fetchedFiles.map(file => ({
                key: file.name,
                modified: +Moment(file.lastModified),
                size: file.size,
            })))
            .then(newFiles => setFiles(newFiles))
            .catch(error => alert("Failed to load scripts"))
    }

    const openFile = (file) => {
        fetch("/api/script?fileName=" + file.key)
            .then(res => res.text())
            .then(content => setOpenFileContent(content))
            .then(() => setOpenFileName(file.key))
            .then(() => setEditorOpen(true))
            .catch(error => alert("Failed to open the file"));
    }

    useEffect(() => {
        loadFiles();
    }, []);

    if (editorOpen) {
        return <ScriptEditor fileName={openFileName} content={openFileContent} commitFile={handleCommitFile} />
    } else {
        return (
            <React.Fragment>
                <CssBaseline />
                <Container maxWidth="false">
                    <Box sx={{ height: 20 }} />
                    <TextField
                        fullWidth
                        label="Repo URL"
                        defaultValue={getRepositoryUrl()}
                        variant="standard"
                        size="small"
                        InputProps={{ readOnly: true }}
                    />
                    <Box sx={{ height: 20 }} />
                    <Explorer files={files} openFile={openFile} />
                </Container>
            </React.Fragment>
        );
    }
}