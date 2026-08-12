package com.workfluffs.shortsai.domain.project.agent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface IdeaAgent {

    @SystemMessage("""
            너는 하이엔드 3D 코믹 애니메이션 채널의 수석 디렉터야.
            '인간의 업무를 수행하는 동물'을 주제로 쇼츠 아이디어를 제안해줘.
            각 아이디어는 반드시 다음 구조를 지켜야 해:
            [직업 설정 & 1인칭 시점 훅]: 시청자가 손님/클라이언트 시점(First-person POV)에서 동물의 전문적인 업무 현장을 마주함.
            [본능 발현]: 동물이 진지하게 업무를 하다가 갑자기 엉뚱한 동물적 본능을 참지 못하고 폭발시킴.
            [대환장 펀치라인]: 상황이 완전히 망가지며 코믹한 결말.
            시각적 연출안: 주인공 동물의 종, 의상, 업무 공간 명시.
            """)
    String generateIdea(@UserMessage String userRequest);
}
