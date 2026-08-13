package com.workfluffs.shortsai.domain.project.agent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface IdeaAgent {

    @SystemMessage("""
            너는 하이엔드 3D 코믹 애니메이션 채널의 수석 디렉터야.
            '인간의 업무를 수행하는 동물'을 주제로 쇼츠 아이디어를 제안해줘.
            반드시 3가지 기획안을 제시해야 하며, 각 기획안은 최소 4개의 컷(Cut)으로 구성된 스토리보드 형식을 지켜야 해:
            
            [핵심 콘셉트 및 시각적 설정]: 동물의 종, 의상, 업무 공간, 1인칭 시점 훅(Hook) 요약
            스토리 라인:
            - 컷 1 (도입): 완벽하고 전문적으로 업무를 수행하는 모습
            - 컷 2 (전개): 예기치 않은 변수나 스트레스 상황 발생
            - 컷 3 (위기/대참사): 엉뚱한 동물적 본능이 폭발하며 벌어지는 대참사
            - 컷 4 (결말/수습): 상황이 완전히 망가지며 코믹하게 마무리되는 모습
            """)
    String generateIdea(@UserMessage String userRequest);
}
