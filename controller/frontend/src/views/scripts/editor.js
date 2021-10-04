import React from 'react';
import Editor from '@monaco-editor/react';
import { useRef } from 'react';
import { Button, Container, CssBaseline, Grid } from '@mui/material';

export default function ScriptEditor(props) {
    const { fileName, content, saveFile } = props;
    const editorRef = useRef(null);

    const extractModelRef = (editor, monaco) => {
        editorRef.current = editor;
    }

    const saveContent = (event) => {
        let newContent = editorRef.current.getValue();
        saveFile(fileName, newContent);
    }

    return (
        <React.Fragment>
            <Container>
                <Editor
                    height="80vh"
                    theme="vs-dark"
                    defaultLanguage="javascript"
                    defaultValue={content}
                    onMount={extractModelRef}
                    options={{ fontSize: 15 }}
                />
                <Button style={{ float: "right" }} onClick={saveContent} variant="contained" size="medium">Save</Button>
            </Container>
        </React.Fragment>
    );
}