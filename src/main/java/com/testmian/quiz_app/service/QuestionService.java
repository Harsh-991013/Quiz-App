package com.testmian.quiz_app.service;

import com.testmian.quiz_app.dto.ImportResult;
import com.testmian.quiz_app.entity.*;
import com.testmian.quiz_app.repository.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final CategoryRepository categoryRepository;
    private final DifficultyRepository difficultyRepository;
    private final QuestionTypeRepository questionTypeRepository;
    private final OptionRepository optionRepository;

    public QuestionService(
            QuestionRepository questionRepository,
            CategoryRepository categoryRepository,
            DifficultyRepository difficultyRepository,
            QuestionTypeRepository questionTypeRepository,
            OptionRepository optionRepository
    ) {
        this.questionRepository = questionRepository;
        this.categoryRepository = categoryRepository;
        this.difficultyRepository = difficultyRepository;
        this.questionTypeRepository = questionTypeRepository;
        this.optionRepository = optionRepository;
    }



    public List<Question> getAllActiveQuestions() {
        return questionRepository.findAll()
                .stream()
                .filter(q -> q.getDeletedAt() == null && Boolean.TRUE.equals(q.getIsActive()))
                .toList();
    }

    public List<Question> getDeletedQuestions() {
        return questionRepository.findAll()
                .stream()
                .filter(q -> q.getDeletedAt() != null)
                .toList();
    }

    public Optional<Question> getQuestionById(Integer id) {
        return questionRepository.findById(id)
                .filter(q -> q.getDeletedAt() == null);
    }

    public Question createQuestion(Question question) {
        question.setCreatedAt(LocalDateTime.now());
        question.setDeletedAt(null);
        question.setIsActive(true);
        return questionRepository.save(question);
    }

    public Question updateQuestion(Integer id, Question updatedQuestion) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        question.setQuestionText(updatedQuestion.getQuestionText());
        question.setCategory(updatedQuestion.getCategory());
        question.setDifficulty(updatedQuestion.getDifficulty());
        question.setQuestionType(updatedQuestion.getQuestionType());
        question.setIsActive(updatedQuestion.getIsActive());
        question.setUpdatedAt(LocalDateTime.now());
        return questionRepository.save(question);
    }

    public void softDeleteQuestion(Integer id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        question.setDeletedAt(LocalDateTime.now());
        question.setIsActive(false);
        questionRepository.save(question);
    }

    public Question restoreQuestion(Integer id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        if (question.getDeletedAt() != null) {
            question.setDeletedAt(null);
            question.setIsActive(true);
            questionRepository.save(question);
        }
        return question;
    }

    public Question deactivateQuestion(Integer id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        question.setIsActive(false);
        questionRepository.save(question);
        return question;
    }

    public Question activateQuestion(Integer id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        question.setIsActive(true);
        questionRepository.save(question);
        return question;
    }

    public ImportResult importQuestionsFromExcel(MultipartFile file) {
        ImportResult result = new ImportResult();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getPhysicalNumberOfRows() <= 1) {
                result.addError("Excel sheet is empty or missing data rows.");
                return result;
            }

            int importedCount = 0;
            DataFormatter formatter = new DataFormatter();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    String categoryName = formatter.formatCellValue(row.getCell(0)).trim();
                    String difficultyKey = formatter.formatCellValue(row.getCell(1)).trim();
                    String typeKey = formatter.formatCellValue(row.getCell(2)).trim();
                    String questionText = formatter.formatCellValue(row.getCell(3)).trim();
                    String optionA = formatter.formatCellValue(row.getCell(4)).trim();
                    String optionB = formatter.formatCellValue(row.getCell(5)).trim();
                    String optionC = formatter.formatCellValue(row.getCell(6)).trim();
                    String optionD = formatter.formatCellValue(row.getCell(7)).trim();
                    String correctOption = formatter.formatCellValue(row.getCell(8)).trim().toUpperCase();

                    // Validation checks
                    if (questionText.isEmpty()) throw new Exception("Missing question text");
                    if (categoryName.isEmpty() || difficultyKey.isEmpty() || typeKey.isEmpty())
                        throw new Exception("Category/Difficulty/Type cannot be blank");
                    if (optionA.isEmpty() || optionB.isEmpty() || optionC.isEmpty() || optionD.isEmpty())
                        throw new Exception("All four options must be filled");
                    if (!List.of("A", "B", "C", "D").contains(correctOption))
                        throw new Exception("Correct option must be A, B, C, or D");
                    if (questionRepository.existsByQuestionText(questionText))
                        throw new Exception("Duplicate question found: " + questionText);

                    // Find or create category/difficulty/type
                    Category category = categoryRepository.findByCategoryName(categoryName)
                            .orElseGet(() -> {
                                Category c = new Category();
                                c.setCategoryName(categoryName);
                                c.setCreatedAt(LocalDateTime.now());
                                return categoryRepository.save(c);
                            });

                    Difficulty difficulty = difficultyRepository.findByDifficultyKey(difficultyKey.toLowerCase())
                            .orElseGet(() -> {
                                Difficulty d = new Difficulty();
                                d.setDifficultyKey(difficultyKey.toLowerCase());
                                d.setDisplayName(difficultyKey);
                                d.setCreatedAt(LocalDateTime.now());
                                return difficultyRepository.save(d);
                            });

                    QuestionType questionType = questionTypeRepository.findByTypeKey(typeKey.toLowerCase())
                            .orElseGet(() -> {
                                QuestionType qt = new QuestionType();
                                qt.setTypeKey(typeKey.toLowerCase());
                                qt.setDisplayName(typeKey);
                                qt.setCreatedAt(LocalDateTime.now());
                                return questionTypeRepository.save(qt);
                            });

                    // Save question
                    Question question = new Question();
                    question.setCategory(category);
                    question.setDifficulty(difficulty);
                    question.setQuestionType(questionType);
                    question.setQuestionText(questionText);
                    question.setIsActive(true);
                    question.setCreatedAt(LocalDateTime.now());
                    Question savedQuestion = questionRepository.save(question);

                    // Save options
                    List<Option> options = List.of(
                            new Option(savedQuestion, optionA, correctOption.equals("A")),
                            new Option(savedQuestion, optionB, correctOption.equals("B")),
                            new Option(savedQuestion, optionC, correctOption.equals("C")),
                            new Option(savedQuestion, optionD, correctOption.equals("D"))
                    );

                    optionRepository.saveAll(options);
                    importedCount++;
                    result.incrementSuccess();

                } catch (Exception e) {
                    result.addError("Row " + (i + 1) + ": " + e.getMessage());
                }
            }

            workbook.close();

            if (result.getFailedCount() == 0)
                result.addError("Import completed. All " + importedCount + " rows imported successfully.");
            else
                result.addError("Import completed with " + result.getFailedCount() + " issues.");

        } catch (Exception e) {
            result.addError("Failed to import Excel file: " + e.getMessage());
        }

        return result;
    }

    // Utility to safely read cell values (kept from your original version)
    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }
}
