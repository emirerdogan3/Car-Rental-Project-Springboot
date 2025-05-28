package com.rentacar.service.impl;

import com.rentacar.dto.ColorDTO;
import com.rentacar.entity.Color;
import com.rentacar.mapper.ColorMapper;
import com.rentacar.repository.ColorRepository;
import com.rentacar.service.ColorService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ColorServiceImpl implements ColorService {

    private final ColorRepository colorRepository;
    private final ColorMapper colorMapper;

    @Override
    public List<ColorDTO> getAllColors() {
        return colorRepository.findAll().stream()
                .map(colorMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ColorDTO> getColorDtoById(Integer id) {
        return colorRepository.findById(id).map(colorMapper::toDto);
    }

    @Override
    public Optional<Color> getColorById(Integer id) {
        return colorRepository.findById(id);
    }

    @Override
    @Transactional
    public ColorDTO createColor(ColorDTO colorDTO) {
        Color color = colorMapper.toEntity(colorDTO);
        return colorMapper.toDto(colorRepository.save(color));
    }

    @Override
    @Transactional
    public ColorDTO updateColor(Integer id, ColorDTO colorDTO) {
        Color existingColor = colorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Color not found with id: " + id));
        existingColor.setColorName(colorDTO.getColorName());
        return colorMapper.toDto(colorRepository.save(existingColor));
    }

    @Override
    @Transactional
    public void deleteColor(Integer id) {
        if (!colorRepository.existsById(id)) {
            throw new EntityNotFoundException("Color not found with id: " + id);
        }
        colorRepository.deleteById(id);
    }
} 