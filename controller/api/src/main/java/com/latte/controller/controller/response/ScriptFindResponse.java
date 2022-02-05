package com.latte.controller.controller.response;

import com.latte.controller.dto.FileInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ScriptFindResponse {
    private List<FileInfo> fileInfos;

    public static ScriptFindResponse from(List<FileInfo> fileInfos) {
        return new ScriptFindResponse(fileInfos);
    }
}
