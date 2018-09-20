package com.app.MBox.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class csvParseResultDto {

    private String invalidFormatDetectedRows;

    private String invalidEmailFormatDetectedRows;

    private String maxLengthOfEmailRows;

    private String maxLengthOfArtistNameRows;

    private String artistLimitExceded;

    private int number;

    private int artistAdded;

    private boolean hasErrors;
}
