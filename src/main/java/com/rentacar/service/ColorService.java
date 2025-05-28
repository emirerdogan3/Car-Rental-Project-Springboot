package com.rentacar.service;

import com.rentacar.dto.ColorDTO;
import com.rentacar.entity.Color;

import java.util.List;
import java.util.Optional;

public interface ColorService {
    List<ColorDTO> getAllColors();
    Optional<ColorDTO> getColorDtoById(Integer id);
    ColorDTO createColor(ColorDTO colorDTO);
    ColorDTO updateColor(Integer id, ColorDTO colorDTO);
    void deleteColor(Integer id);
    Optional<Color> getColorById(Integer id); // For internal use
} 